package org.hhplus.ecommerce.item.service;

import lombok.Getter;

@Getter
public class TmpItemDomainAndCnt {

    private final ItemDomain itemDomain;
    private final int cnt;

    public TmpItemDomainAndCnt(ItemDomain itemDomain, int cnt) {
        this.itemDomain = itemDomain;
        this.cnt = cnt;
    }
}
