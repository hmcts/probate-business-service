package uk.gov.hmcts.probate.services.invitation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotifyPersonalisationEscapeServiceTest {
    @Test
    void testNoEscapes() {
        Map<String, String> noEscapes = Map.of();
        final NotifyPersonalisationEscapeService service = new NotifyPersonalisationEscapeService(noEscapes);

        final String input = "some string";
        final String result = service.escape(input);

        assertThat(result, not(sameInstance(input)));
        assertThat(result, equalTo(input));
    }

    @Test
    void testSimpleReplacement() {
        Map<String, String> replace = Map.of("b", "BBBBB");
        final NotifyPersonalisationEscapeService service = new NotifyPersonalisationEscapeService(replace);

        final String input = "abc";
        final String expected = "aBBBBBc";

        final String result = service.escape(input);

        assertThat(result, equalTo(expected));
    }

    @Test
    void testSimpleRemoval() {
        Map<String, String> replace = Map.of("b", "");
        final NotifyPersonalisationEscapeService service = new NotifyPersonalisationEscapeService(replace);

        final String input = "abc";
        final String expected = "ac";

        final String result = service.escape(input);

        assertThat(result, equalTo(expected));
    }

    @Test
    void testOnlySingleCodepointInput() {
        Map<String, String> replace = Map.of("bb", "");
        assertThrows(RuntimeException.class, () -> new NotifyPersonalisationEscapeService(replace));

    }

    // This is a completely niche test - it's checking that given a zero-width-joiner emoji
    // sequence that we can replace codepoints within the input, and at least one of those
    // codepoints is outside the normal range of a java char.
    @Test
    void testZWJSequence() {
        // we replace:
        //   the female symbol with  male symbol
        //   the light skin tone with dark skin tone
        Map<String, String> replace = Map.of(
            "♀", "♂",
            "🏻", "🏿");
        final NotifyPersonalisationEscapeService service = new NotifyPersonalisationEscapeService(replace);

        // whilst the input/output might appear to be single graphical glyphs,they are actually
        // composed of five codepoints in sequence, and require 7 char values to store.

        // this is the emoji "Woman Construction Worker: Light Skin Tone"
        // U+1f477 👷 "Construction Worker", encoded as two java chars 0xd83d 0xdc77
        // U+1f3fb 🏻 "Emoji Modified Fitzpatrick Type-1-2, ditto 0xd83c 0xdffb
        // U+200D . "Zero Width Joiner", 0x200d
        // U+2640 ♀ "Female Sign", 0x2640
        // U+fe0f ◌️ "Variation Selector-16 (VS16)", 0xfe0f
        final String input = "👷🏻‍♀️";
        // this is the emoji "Man Construction Worker: Dark Skin Tone"
        // U+1f477 👷 "Construction Worker", encoded as two java chars 0xd83d 0xdc77
        // U+1f3ff 🏿 "Emoji Modified Fitzpatrick Type-6, ditto 0xd83c 0xdfff
        // U+200D . "Zero Width Joiner", 0x200d
        // U+2642 ♂ "Male Sign", 0x2642
        // U+fe0f ◌️ "Variation Selector-16 (VS16)", 0xfe0f
        final String expected = "👷🏿‍♂️";

        assertThat(input, hasLength(7));
        assertThat(input.codePoints().count(), equalTo(5L));
        assertThat(expected, hasLength(7));
        assertThat(expected.codePoints().count(), equalTo(5L));

        final String result = service.escape(input);

        assertThat(input.codePointCount(0, input.length()), equalTo(5));
        assertThat(result, equalTo(expected));
    }
}
