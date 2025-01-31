package uk.gov.hmcts.probate.services.businessvalidation.validators;

import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.layout.element.IElement;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class CustomEventListener implements IEventListener {
    private final List<IElement> elements = new ArrayList<>();

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof TextRenderInfo) {
            // Capture text elements or other types of elements as needed
            TextRenderInfo textRenderInfo = (TextRenderInfo) data;
            // Add logic to capture Table and Cell elements
            // This is a placeholder, you need to implement the actual logic to capture Table and Cell elements
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return EnumSet.of(EventType.RENDER_TEXT);
    }

    public List<IElement> getElements() {
        return elements;
    }
}
