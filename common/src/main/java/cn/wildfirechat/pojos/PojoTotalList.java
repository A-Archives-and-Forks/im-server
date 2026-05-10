package cn.wildfirechat.pojos;

import java.util.List;

public class PojoTotalList {
    public int total;
    public List<String> list;

    public PojoTotalList() {
    }

    public PojoTotalList(int total, List<String> list) {
        this.total = total;
        this.list = list;
    }
}
