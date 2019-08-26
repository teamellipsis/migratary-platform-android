package com.teamellipsis.dynamic;

import java.io.Serializable;

public interface DynamicApp extends Serializable {
    long serialVersionUID = 7165582299168504283L;
    String getText();

    void fetchState(Object[] state);

    Object execute(Object[] args);

    Object[] saveState();
}
