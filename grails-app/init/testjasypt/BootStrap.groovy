package testjasypt

import test.Car

class BootStrap {
    def hibernateStringEncryptor
    def init = { servletContext ->

        new Car(name:'Ford', decrypted:'Ford', other: 'Ford' ).save(flush:true)
        new Car(name:'Toyota', decrypted:'Toyota', other: 'Toyota').save(flush:true)
        new Car(name:'Mercedes', decrypted:'Mercedes', other: 'Mercedes').save(flush:true)
        new Car(name:'BMW', decrypted:'BMW', other: 'BMW').save(flush:true)
        new Car(name:'Citroen', decrypted:'Citroen', other: 'Citroen').save(flush:true)

    }
    def destroy = {
    }
}
