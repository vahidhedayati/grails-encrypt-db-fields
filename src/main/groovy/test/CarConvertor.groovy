package test

import grails.util.Holders

import javax.persistence.AttributeConverter

class CarConvertor  implements AttributeConverter<String, String> {

    def hibernateStringEncryptor = Holders.grailsApplication.mainContext.getBean('hibernateStringEncryptor')

    @Override
    public String convertToDatabaseColumn(String attribute) {
        /* perform encryption here */
       return  encrypt(attribute)
    }
    @Override
    public String convertToEntityAttribute(String dbData) {
        /* perform decryption here */
        String returnText =  decrypt(dbData)

        println " --  dbData = ${dbData} = $returnText "
        return returnText
    }

    String decrypt(String name) {
        println "decryptin $name"
        return hibernateStringEncryptor.decrypt(name)
    }

    String encrypt(String name) {
        println "decryptin $name"
        return hibernateStringEncryptor.encrypt(name)
    }
}
