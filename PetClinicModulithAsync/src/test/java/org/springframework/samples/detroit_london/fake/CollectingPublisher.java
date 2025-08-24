package org.springframework.samples.detroit_london.fake;

import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class CollectingPublisher implements ApplicationEventPublisher {

    public List<Object> published = new ArrayList<>();

    @Override
    public void publishEvent(Object event) {
        published.add(event);
    }

    @SuppressWarnings("unchecked")
    public <T> T first(Class<T> type) {
        return (T) published.stream().filter(type::isInstance).findFirst().orElse(null);
    }

}
