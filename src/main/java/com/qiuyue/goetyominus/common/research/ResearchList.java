package com.qiuyue.goetyominus.common.research;

import com.Polarice3.Goety.common.research.Research;

public class ResearchList {
    public static final Research BASTION = new Research("bastion");

    public static void register() {
        com.Polarice3.Goety.common.research.ResearchList.RESEARCH_LIST.put("bastion", BASTION);
    }
}