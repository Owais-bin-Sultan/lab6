package twitter;

import java.util.*;
import java.util.stream.Collectors;

public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();  // Convert author to lowercase
            Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet));

            if (!mentionedUsers.isEmpty()) {
                followsGraph.putIfAbsent(author, new HashSet<>());

                // Add all mentioned users to the author's follows set
                for (String mentionedUser : mentionedUsers) {
                    String mentionedUserLower = mentionedUser.toLowerCase();
                    if (!mentionedUserLower.equals(author)) { // Ensure author doesn't follow themselves
                        followsGraph.get(author).add(mentionedUserLower);
                    }
                }
            }
        }

        return followsGraph;
    }



    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        // Count followers for each user
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            String follower = entry.getKey(); // who is following
            Set<String> followedUsers = entry.getValue(); // who they follow
            for (String user : followedUsers) {
                followerCount.put(user, followerCount.getOrDefault(user, 0) + 1);
            }
        }

        // Add users with no followers to ensure they are considered with 0 followers
        for (String user : followsGraph.keySet()) {
            followerCount.putIfAbsent(user, 0);
        }

        // Debugging: print follower counts to check correctness
        System.out.println("Follower counts: " + followerCount);

        // Sort by follower count in descending order
        return followerCount.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))  // Descending order
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
