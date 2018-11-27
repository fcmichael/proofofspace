package blockchain;

import org.apache.commons.codec.digest.DigestUtils;

class HashService {

    static String sha256(String string){
        return DigestUtils.sha256Hex(string);
    }
}
