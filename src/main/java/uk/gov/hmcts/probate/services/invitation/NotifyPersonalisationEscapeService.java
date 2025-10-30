package uk.gov.hmcts.probate.services.invitation;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class NotifyPersonalisationEscapeService {
    private final Map<Integer, List<Integer>> escapes;

    public NotifyPersonalisationEscapeService(
            final Map<String, String> escapes) {
        final Map<Integer, List<Integer>> codePointEscapes = new HashMap<>();
        for (final var escape : escapes.entrySet()) {
            final String key = escape.getKey();
            final String val = escape.getValue();

            if (key.codePointCount(0, key.length()) != 1) {
                throw new IllegalArgumentException("Must only be a single codepoint key");
            }
            final Integer keyCodePoint = key.codePointAt(0);
            final List<Integer> valueCodePoints = val.codePoints().boxed().toList();
            log.info("Will map [{}] to [{}] ([{}] to [{}])",
                    key, val, keyCodePoint,
                    valueCodePoints.stream().map(String::valueOf).collect(Collectors.joining(",")));
            codePointEscapes.put(keyCodePoint, valueCodePoints);
        }
        this.escapes = Map.copyOf(codePointEscapes);
    }

    public String escape(final String parameter) {
        // we operate on a per-codepoint basis because there are characters which
        // in theory we may need to replace which fall outside the 16-bit limit of
        // java's char/Character type.
        if (parameter == null) {
            return null;
        }
        return parameter.codePoints()
            .mapToObj(this::escapeCodepoint)
            .flatMap(Collection::stream)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    List<Integer> escapeCodepoint(final Integer codepoint) {
        final List<Integer> result = new ArrayList<>();
        if (escapes.containsKey(codepoint)) {
            result.addAll(escapes.get(codepoint));
        } else {
            result.add(codepoint);
        }
        return result;
    }
}
