package business.observertooling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subject
{
  private final Map<EventType, List<Listener>> listeners = new HashMap<>();

  public void addListener(EventType eventType, Listener listener) {
    listeners
        .computeIfAbsent(eventType, k -> new ArrayList<>())
        .add(listener);
  }

  public void removeListener(EventType eventType, Listener listener) {
    List<Listener> listenersForEvent = listeners.get(eventType);
    if (listenersForEvent != null) {
      listenersForEvent.remove(listener);
    }
  }

  protected void notifyListeners(EventType eventType, Object arg) {
    List<Listener> listenersForEvent = listeners.getOrDefault(eventType, List.of());
    for (Listener listener : listenersForEvent) {
      listener.update(arg);
    }
  }}
