package com.vi.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SqsService {
	public List<String> consumeMessages();
}
