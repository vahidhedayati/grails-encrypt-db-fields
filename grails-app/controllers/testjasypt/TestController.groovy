package testjasypt

import grails.converters.JSON
import test.Car

class TestController {

    def hibernateStringEncryptor

    def index() {

        Map output =[:]
        String carName='Ford'

        output.lookinUp=carName

        /**
         * This is called forward walking get the object off DB
         * let hibernate UserType or getters convert the data
         *
         */
        def expensiveButWorks=Car.findAll().findAll{it.name==carName}
        output.expensiveButWorks="Car.findAll().findAll{it.name==carName}:  Found car expensiveButWorks  = ${expensiveButWorks?.name}"

        def expensiveButWorks1=Car.findAll().findAll{it.decrypted==carName}
        output.expensiveButWorks1=" Car.findAll().findAll{it.decrypted==carName}: Found car expensiveButWorks1 = ${expensiveButWorks1?.name}"

        def expensiveButWorks2=Car.findAll().findAll{it.other==carName}
        output.expensiveButWorks2= "Car.findAll().findAll{it.other==carName} Found car expensiveButWorks2 = ${expensiveButWorks2?.name}"

        /**
         * The real issue is even if you encrypt car name now salt will change encryption
         * so original stored value will not even match this
         */
        String encCar = hibernateStringEncryptor.encrypt(carName)
        output.encryptingCarName1="hibernateStringEncryptor.encrypt(carName) = encCar ${encCar}"
        //But their decryption will be identical
        String decCar1 =  hibernateStringEncryptor.decrypt(encCar)
        output.decryptingCarName1="hibernateStringEncryptor.decrypt(encCar) = ${decCar1} "


        sleep(20)
        String encCar1 = hibernateStringEncryptor.encrypt(carName)
        output.encryptingCarName2="hibernateStringEncryptor.encrypt(carName) = ${encCar1}"

        String decCar2 =  hibernateStringEncryptor.decrypt(encCar1)
        output.decryptingCar2="hibernateStringEncryptor.decrypt(encCar1) = ${decCar2}"


        def decryptedCars = Car.findAll().decrypted
        output.decryptedCars="Works All decrypted cars:Car.findAll().decrypted = ${decryptedCars}"


        def cars = Car.findAllByDecrypted(carName)

        output.notWorking1=" Car.findAllByDecrypted(carName) 0 found = ${cars}"

        def cars1 = Car.findAllByName(encCar)

        output.notWorking2=" Car.findAllByName(encCar) 0 found = ${cars1}"

        def query = """ from Car where decrypted  =:carName"""
        def cars2=Car.executeQuery(query,[carName:carName])
        output.notWorking3=" Car.executeQuery( from Car where decrypted  =:carName,[carName:$carName]) 0 car = ${cars2}"


        def cars4 = Car.where{decrypted =~ carName}?.findAll()
        output.notWorking4=" Car.where{decrypted =~ carName}?.findAll() 0 cars  ${cars4}"

        def query2 = {
            or{
                ilike "decrypted", "%${carName}%"
            }

        }
        def results = Car?.createCriteria()?.list(query2)
        output.notWorking5=" Car?.createCriteria()?.list($query2) Found car 0 = ${results}"

        def cars5 = Car.where{other =~ carName}?.findAll()
        output.notWorking6=" Car.where{other =~ carName}?.findAll() Found car 0 = ${results}"


        JSON json = output as JSON
        json.prettyPrint = true
        json.render response

    }
    def decrypt(String carName) {
        return hibernateStringEncryptor.encrypt(carName)
    }
}
