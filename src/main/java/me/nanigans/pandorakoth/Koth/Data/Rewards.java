package me.nanigans.pandorakoth.Koth.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rewards{

    private List<Map<String, String>> rewards = new ArrayList<>();

    public Rewards(KothEvent kothEvent) {
        final List<Map<?, ?>> mapList = kothEvent.getYaml().getData().getMapList(kothEvent.getEventName() + ".rewards");
        for (Map<?, ?> map : mapList) {
            rewards.add((Map<String, String>) map);
        }
    }


    public Map<String, String> getRewardFromList(String uuid){
        for (Map<String, String> map1 : rewards) {
            if(map1.get("ID").equals(uuid))
                return map1;
        }
        return null;
    }

    public void setRewards(List<Map<String, String>> rewards) {
        this.rewards = rewards;
    }

    public List<Map<String, String>> getRewardList() {
        return rewards;
    }
}
