package com.bib.TrustBuy.system.bl.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {
    private String status;
    private Integer actorId;
    private String actorRole;
}

