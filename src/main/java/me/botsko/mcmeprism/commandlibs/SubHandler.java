package me.botsko.mcmeprism.commandlibs;

import java.util.List;

public interface SubHandler {
    public void handle(CallInfo call);

    public List<String> handleComplete(CallInfo call);
}