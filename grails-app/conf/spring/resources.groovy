import org.jasypt.hibernate4.encryptor.HibernatePBEStringEncryptor

// Place your Spring DSL code here
beans = {
      hibernateStringEncryptor(HibernatePBEStringEncryptor) {
      registeredName = "gormEncryptor"
      algorithm = "PBEWITHSHA256AND128BITAES-CBC-BC"
      providerName= "BC"
      password = "s3kr1t"
      keyObtentionIterations = 1000
  }
}

