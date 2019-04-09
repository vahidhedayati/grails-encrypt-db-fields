
Controller provided will produce something like this

```
{
  "lookinUp": "Ford",
  "expensiveButWorks": "Car.findAll().findAll{it.name==carName}:  Found car expensiveButWorks  = [Ford]",
  "expensiveButWorks1": " Car.findAll().findAll{it.decrypted==carName}: Found car expensiveButWorks1 = [Ford]",
  "expensiveButWorks2": "Car.findAll().findAll{it.other==carName} Found car expensiveButWorks2 = [Ford]",
  "encryptingCarName1": "hibernateStringEncryptor.encrypt(carName) = encCar iUt4FEHLzSOEfoCDdXdQQSYhE2gijesL1jx+o5hOizQ=",
  "decryptingCarName1": "hibernateStringEncryptor.decrypt(encCar) = Ford ",
  "encryptingCarName2": "hibernateStringEncryptor.encrypt(carName) = bEXVRvTVykxhzbDwidqS8TC9xNM078qow9U4hnn8SbU=",
  "decryptingCar2": "hibernateStringEncryptor.decrypt(encCar1) = Ford",
  "decryptedCars": "Works All decrypted cars:Car.findAll().decrypted = [Ford, Toyota, Mercedes, BMW, Citroen]",
  "notWorking1": " Car.findAllByDecrypted(carName) 0 found = []",
  "notWorking2": " Car.findAllByName(encCar) 0 found = []",
  "notWorking3": " Car.executeQuery( from Car where decrypted  =:carName,[carName:Ford]) 0 car = []",
  "notWorking4": " Car.where{decrypted =~ carName}?.findAll() 0 cars  []",
  "notWorking5": " Car?.createCriteria()?.list() Found car 0 = []",
  "notWorking6": " Car.where{other =~ carName}?.findAll() Found car 0 = []"
}
```

![db output](https://raw.githubusercontent.com/vahidhedayati/grails-encrypt-db-fields/master/docs/db-data.png)

When running HQL or findBy Or Criteria query. Hibernate will attempt to query the DB directly the data on db as per above 
is still encrypted.

The magic hibernate does either via the [getters](https://github.com/vahidhedayati/grails-encrypt-db-fields/blob/master/grails-app/domain/test/Car.groovy#L30-L32) or via even more hidden away overrides of UserType as done by 
[grails-jasypt](https://github.com/dtanner/grails-jasypt/blob/d997d1e144dd0816afb0e9fe87072f2854bb1292/src/main/groovy/com/bloomhealthco/jasypt/DefaultParametersUserType.groovy) 
plugin or by local file [EncryptedString](https://github.com/vahidhedayati/grails-encrypt-db-fields/blob/master/src/main/groovy/test/EncryptedString.groovy)
simply are transformations that happen to end objects as retrieved from DB.

So Unless you have some way of decrypting the end data from within the SQL there isn't going to be an easy way, as an example mysql provides internal 
encrypt decrypt options which could be used in raw sql or possibly hql. Beyond this it is really about hibernate doing its magic to transform the raw DB 
back to what it should be human readable by this point too late for a query hence the findAll findAll.

This over 2 million rows is going to too expensive but on relative low recods things could be done 
 
 
 Perhaps the main reason there is such difficulty is as you can see how Ford got encrypted to two very 
 different values but decrypted back to ford. Excellent for keeping data safe not so good for being able to make queries 
 to match underlying encrypted string.
 
 ```
 
  "encryptingCarName1": "hibernateStringEncryptor.encrypt(carName) = encCar iUt4FEHLzSOEfoCDdXdQQSYhE2gijesL1jx+o5hOizQ=",
   "decryptingCarName1": "hibernateStringEncryptor.decrypt(encCar) = Ford ",
   "encryptingCarName2": "hibernateStringEncryptor.encrypt(carName) = bEXVRvTVykxhzbDwidqS8TC9xNM078qow9U4hnn8SbU=",
   "decryptingCar2": "hibernateStringEncryptor.decrypt(encCar1) = Ford",


```