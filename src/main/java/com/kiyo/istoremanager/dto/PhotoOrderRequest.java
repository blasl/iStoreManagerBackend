package com.kiyo.istoremanager.dto;

import java.util.List;

public record PhotoOrderRequest(List<Long> photoIds) {}