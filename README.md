# techbulls secure logging 
This library allows you to log information in secure fashion. It protecets from sensitive information being logged in the log file by masking the output of such information. The library relies on declarative style using annotation to mark fields which are sensitive and should not be logged as-is in the log. Options are provided to customize the masked output using custom logic.

For example: consider a request model class `LoginRequest` with attributes `username` and `password`. In your login REST API, you log the jsonified request. In case the request came with username as `john.doe@domain.com` and password as `secret123` it will end up logging the request like:
```
Login request: {"username": "john.doe@domain.com", password="secret123"}
```
Note that the password here was printed in the plain text format. The library will allow you to create an output like below instead:
```
Login request: {"username": "john.doe@domain.com", password="xxxxxxx"}
```

The library provides a simple utility methods to convert given object to JSON honoring the secure logging annotations to mask the sensitive data. We ecnourage you to implement a `toString` method on your model class so that any accidental logging in log will lead to maked data being logged.

# Annotations

## @SecureLog
You can annotate a model class with @SecureLog annotation. The annotation provides following attributes,
- *pretty* - Makes the json output of the object to be pretty
- *view* - In case you are using `@JsonView`, you can control what view will be used when generating the secure json output.

## @LogSensitive
Any field that should not be logged as is in the log should be annotated as `@LogSensitive` The secure json output will ensure that the fields marked as sensitive will be masked in the generated output. The annotation provides following attributes,
- *value* - An alternative value that should be printed for the given field. The default is `XXXX`
- *formatter* - Allows you to control masking of the sensitive value by providing a custom `ValueFormatter` which can control how to mask a value before it is getting logged.
- *secureNullValues* - Allows securing the `null` values. If set to `true` the masked value will be used instead of the `null` value.
