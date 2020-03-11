package com.leyou.page.client;

import com.leyou.item.api.SpeclicationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpeclicationClient extends SpeclicationApi {
}
