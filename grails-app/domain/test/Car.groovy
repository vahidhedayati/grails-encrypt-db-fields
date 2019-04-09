package test

import com.bloomhealthco.jasypt.GormEncryptedStringType
import grails.util.Holders

import javax.persistence.Convert

class Car {
    def hibernateStringEncryptor = Holders.grailsApplication.mainContext.getBean('hibernateStringEncryptor')


    //static transients = ['decrypted']

    //@Convert(converter = CarConvertor)
    String name

    String decrypted
    String other

    static constraints = {
        decrypted(nullable: true)
    }
    static mapping = {
        name type: GormEncryptedStringType
       // decrypted formula: 'name'
        //Custom encryption review comments in this class
        other type: EncryptedString
    }

    String getDecrypted() {
        return hibernateStringEncryptor.decrypt(this.decrypted)
    }

    void setDecrypted(String n) {
        this.decrypted=hibernateStringEncryptor.encrypt(n)
    }


    String encrypt(String name) {
        println "decrypting $name"
        return hibernateStringEncryptor.encrypt(name)
    }
    String decrypt(String name) {
        println "decrypting $name"
        return hibernateStringEncryptor.decrypt(name)
    }
/*
    String getName() {
        println "getting name"
        return hibernateStringEncryptor.decrypt(this.name)
    }
*/
}
