package com.seismotech.hashish.impl.sip;

import com.seismotech.hashish.api.Hashing;

public interface SipFactory {
  Hashing sipFor(long k0, long k1);
}
