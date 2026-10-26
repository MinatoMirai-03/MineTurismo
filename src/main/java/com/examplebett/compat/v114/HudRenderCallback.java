//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

import java.util.ArrayList;
import java.util.List;

public interface HudRenderCallback {
    Event EVENT = new Event();

    void onHudRender(float tickDelta);

    final class Event {
        private final List<HudRenderCallback> listeners = new ArrayList<>();

        private Event() {
        }

        public void register(HudRenderCallback listener) {
            this.listeners.add(listener);
        }

        public HudRenderCallback invoker() {
            return tickDelta -> {
                for (HudRenderCallback listener : this.listeners) {
                    listener.onHudRender(tickDelta);
                }
            };
        }
    }
}
*/
//?}
