/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {
 
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "Okay, I think it is reasonable", d3);

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */
    @Test
    public void testWrittenByReturnsSingleTweetForSingleAuthor() {
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        assertEquals(1, result.size());
        assertTrue(result.contains(tweet1));
    }

    @Test
    public void testWrittenByReturnsMultipleTweetsForSameAuthor() {
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(tweet1, tweet3)));
    }

    @Test
    public void testWrittenByReturnsEmptyListForNonexistentAuthor() {
        List<Tweet> result = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInTimespanReturnsAllTweetsWithinSpecifiedRange() {
        Timespan timespan = new Timespan(d1, d3);
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), timespan);
        assertEquals(3, result.size());
    }

    @Test
    public void testInTimespanReturnsEmptyListWhenNoTweetsInRange() {
        Timespan timespan = new Timespan(Instant.parse("2015-01-01T00:00:00Z"), Instant.parse("2015-12-31T23:59:59Z"));
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), timespan);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testContainingReturnsAllTweetsWithSpecifiedWord() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        assertEquals(2, result.size());
    }

    @Test
    public void testContainingReturnsEmptyListForNoMatchingWords() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("nonexistent"));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testContainingReturnsAllTweetsCaseInsensitiveMatch() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("RIVEST"));
        assertEquals(2, result.size());
    }

    @Test
    public void testContainingReturnsEmptyListForEmptyWordList() {
        List<Tweet> result = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList());
        assertTrue(result.isEmpty());
    }

}
