package org.seismotech.hashish.impl.sip;

import org.seismotech.hashish.api.Hashing;

public interface SipFactory {
  Hashing sipFor(long k0, long k1);
}
