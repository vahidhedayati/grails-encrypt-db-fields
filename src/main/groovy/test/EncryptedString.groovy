package test

import grails.converters.JSON
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

/**
 *  UserType is an interface in Hibernate :  org.hibernate.usertype.UserType
 *
 *    When we declare a string type the data transformation happens after
 *    db data has been fetched
 *
 *    Both this manual method and jasypt plugin override what hibernate does for transformation of data between db and
 *    the getter of the field.
 *
 *    The data is then available decrypted after object is fetched - forward walking through objects when fetched works
 *
 *    i.e. findAll().findAll{it.name=='someDecryptedText'}  the key is the findAll initially expensive too expensive
 *
 *
 */
class EncryptedString implements UserType {

    int[] sqlTypes() { [Types.VARCHAR] as int[] }
    Class returnedClass() { String }
    boolean equals(x, y) { x == y }
    int hashCode(x) { x.hashCode() }

    @Override
    Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String str = rs.getString(names[0])
        str ? decrypt(str) : null
    }

    @Override
    void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        st.setString(index, value ? encrypt(value) : null)
    }

    Object deepCopy(value) { value }
    boolean isMutable() { false }
    Serializable disassemble(value) { value }
    def assemble(Serializable cached, owner) { cached }
    def replace(original, target, owner) { original }

    /*
    Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        String str = resultSet.getString(names[0])
        str ? decrypt(str) : null
    }

    void nullSafeSet(PreparedStatement statement, Object value, int index) {
        statement.setString(index, value ? encrypt(value) : null)
    }
    */
    private String encrypt(String plaintext) {
        String salt = 'Salt!'
        String key = "myKey"
        Cipher c = Cipher.getInstance('AES')
        byte[] keyBytes = MessageDigest.getInstance('SHA-1').digest("$salt$key".getBytes())[0..<16]
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, 'AES'))
        [data: c.doFinal(plaintext.bytes).encodeBase64() as String, salt: salt] as JSON
    }

    private String decrypt(String ciphertext) {
        def json = JSON.parse(ciphertext)
        Cipher c = Cipher.getInstance('AES')
        String key = "myKey"
        byte[] keyBytes = MessageDigest.getInstance('SHA-1').digest("${json.salt}${key}".getBytes())[0..<16]
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, 'AES'))
        new String(c.doFinal(json['data'].decodeBase64()))
    }
}