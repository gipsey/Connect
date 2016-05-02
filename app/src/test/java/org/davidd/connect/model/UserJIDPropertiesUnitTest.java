package org.davidd.connect.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UserJIDPropertiesUnitTest {

    @Test
    public void test_UserJIDProperties_worksProperly_whenUserDomainAndResProvided() {
        final String JID = "user@domain/res";

        UserJIDProperties properties = new UserJIDProperties(JID);

        assertEquals("user", properties.getName());
        assertEquals("domain", properties.getDomain());
        assertEquals("res", properties.getResource());
        assertTrue(properties.isNameAndDomainValid());
    }

    @Test
    public void test_UserJIDProperties_worksProperly_whenUserAndDomainProvided() {
        final String JID = "user@domain";

        UserJIDProperties properties = new UserJIDProperties(JID);

        assertEquals("user", properties.getName());
        assertEquals("domain", properties.getDomain());
        assertTrue(properties.getResource().isEmpty());
        assertTrue(properties.isNameAndDomainValid());
    }

    @Test
    public void test_UserJIDProperties_worksProperly_whenUserProvided() {
        final String JID = "user";

        UserJIDProperties properties = new UserJIDProperties(JID);

        assertEquals("user", properties.getName());
        assertTrue(properties.getDomain().isEmpty());
        assertTrue(properties.getResource().isEmpty());
        assertFalse(properties.isNameAndDomainValid());
    }

    @Test
    public void test_UserJIDProperties_worksProperly_whenNothingProvided() {
        final String JID = "";

        UserJIDProperties properties = new UserJIDProperties(JID);

        assertTrue(properties.getName().isEmpty());
        assertTrue(properties.getDomain().isEmpty());
        assertTrue(properties.getResource().isEmpty());
        assertFalse(properties.isNameAndDomainValid());
    }
}
