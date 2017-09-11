package com.deva.github.followers;






import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController()
@RequestMapping("/retrievegithub")
public class FollowerController {
	RestTemplate restTemplate = new RestTemplate();

	@RequestMapping(value="/followers/{githubid}",method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE )	
    public @ResponseBody  ResponseEntity<Object> retrieveFollowers(@PathVariable("githubid") String githubid) {
    	
		GitHubFollower example1[] = restTemplate.getForObject("https://api.github.com/users/"+githubid+"/followers", GitHubFollower[].class);
		int length = example1.length;
		if(length > 5) length = 5;
		GitHubFollower[] finalResponse = new GitHubFollower[length]; 
		for(int i=0; i<length; i++){
			GitHubFollower example = example1[i];
			example.setChildGitHub(new GitHubFollower[1]);
			GitHubFollower[] lstFollowers = getFollowersArrays(example.getFollowersUrl(), example.getChildGitHub(), 3);
		    finalResponse[i] = example;
		}
		return new ResponseEntity<Object>(finalResponse, HttpStatus.OK);
    }
	

	
	private GitHubFollower[] getFollowersArrays(String followerId, GitHubFollower[] lstFollowers, int depth){
		if( depth <=0) { 
			return lstFollowers;
		}else{
			GitHubFollower example1[]  = restTemplate.getForObject("https://api.github.com/users/"+followerId.substring(29,followerId.lastIndexOf("/"))+"/followers", GitHubFollower[].class);
			if(example1.length>0){
				lstFollowers[0]=example1[0];    
				lstFollowers[0].setChildGitHub(new GitHubFollower[1]);
				depth--;
				getFollowersArrays(example1[0].getFollowersUrl(), lstFollowers[0].getChildGitHub(), depth);
			}else{
				return lstFollowers;
			}
		}
		return lstFollowers;
		
	}
	
	
	
}
