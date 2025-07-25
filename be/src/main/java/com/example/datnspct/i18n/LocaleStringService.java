package com.example.datnspct.i18n;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public interface LocaleStringService {
    Locale getCurrentLocale();

    String getMessage(String messageCode, String defaultMessage, Object... params);
}
