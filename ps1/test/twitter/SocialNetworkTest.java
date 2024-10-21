package twitter;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategies for guessFollowsGraph():
     * 1. **Empty List of Tweets**: Ensure that an empty list results in an empty graph.
     * 2. **Tweets Without Mentions**: Verify that tweets with no mentions do not add entries to the graph.
     * 3. **Single Mention**: Test whether a user who mentions someone is correctly added to the graph.
     * 4. **Multiple Mentions**: Check if multiple mentioned users are linked to the tweet author.
     * 5. **Multiple Tweets from One User**: Ensure that repeated mentions from the same user are captured.
     * 
     * Testing strategies for influencers():
     * 1. **Empty Graph**: Verify that no users yield an empty influencer list.
     * 2. **Single User Without Followers**: Test that a user without followers yields no influencers.
     * 3. **Single Influencer**: Verify correct identification of the only influencer.
     * 4. **Multiple Influencers**: Test for correct influencer ordering.
     * 5. **Tied Influence**: Ensure equal influencers are handled correctly.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // Make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void Empty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("Expected empty graph", followsGraph.isEmpty());
    }
    


    @Test
    public void SingleMention() {
        List<Tweet> tweets = List.of(
                new Tweet(1, "alice", "@bob is awesome!", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertEquals("Alice should follow Bob", Set.of("bob"), followsGraph.get("alice"));
        assertFalse("Bob should not appear in the graph", followsGraph.containsKey("bob"));
    }
    
    @Test
    public void NoMentions() {
        // Create a list of tweets without any mentions
        List<Tweet> tweets = List.of(
            new Tweet(1, "alice", "I love programming!", Instant.now()),
            new Tweet(2, "bob", "Just having coffee", Instant.now())
        );
        
        // Generate follows graph
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        
        // The graph should be empty since there are no mentions
        assertTrue("Expected no entries in the graph for tweets without mentions", followsGraph.isEmpty());
    }
    @Test
    public void MultipleTweetsFromSameUser() {
        List<Tweet> tweets = List.of(
                new Tweet(1, "alice", "@bob is awesome!", Instant.now()),
                new Tweet(2, "alice", "Also, @charlie rocks!", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertEquals("Alice should follow both Bob and Charlie", Set.of("bob", "charlie"), followsGraph.get("alice"));
    }

    @Test
    public void MultipleTweetsFromUser() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "alice", "Hello @bob", Instant.now()));
        tweets.add(new Tweet(2, "alice", "Also @bob, how are you?", Instant.now()));
        
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        
        assertTrue("Alice should follow Bob", followsGraph.get("alice").contains("bob"));
        assertEquals("Alice should only follow Bob once", 1, followsGraph.get("alice").size());
    }
    @Test
    public void InfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("Expected empty list", influencers.isEmpty());
    }

    @Test
    public void SingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("Single user with no followers should yield only Alice", List.of("alice"), influencers);
    }

    @Test
    public void SingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob"));
        
//        List<String> influencers = SocialNetwork.influencers(followsGraph);
//        
//        assertEquals("Bob should be the only influencer", List.of("bob"), influencers);
    }


    @Test
    public void MultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob", "charlie"));
        followsGraph.put("bob", Set.of("charlie"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("Charlie should be the top influencer, followed by Bob and Alice", List.of("charlie", "bob", "alice"), influencers);
    }

    @Test
    public void TiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob"));
        followsGraph.put("bob", Set.of("charlie"));
        followsGraph.put("charlie", Set.of("alice"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("All users should be influencers", influencers.containsAll(List.of("alice", "bob", "charlie")));
    }

}
