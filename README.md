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

# Example
```java
public class SafeLogging {
    @SecureLog
    public static class UserInfo {

        @LogSensitive(value = "XXXXX XXXXX")
        private String name;

        @LogSensitive(formatter = EmailFormatter.class)
        private String email;

        @LogSensitive(formatter = MobileFormatter.class)
        private String mobile;

        @JsonProperty("member_id")
        private String memberId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        @Override
        public String toString() {
            return SecureJson.toJson(this);
        }
    }

    private static class EmailFormatter implements ValueFormatter {
        @Override
        public String format(Object value, String secureValue) {
            return value.toString().replaceAll("[A-Za-z0-9\\_\\-\\.]+@", "xxxxxx@");
        }
    }

    private static class MobileFormatter implements ValueFormatter {
        @Override
        public String format(Object value, String secureValue) {
            String mobile = value.toString();
            if (mobile.length() <= 2) {
                return mobile;
            }

            return "XXXXXXX" + mobile.substring(mobile.length() - 2);
        }
    }

    public static void main(String[] args) {
        UserInfo user = new UserInfo();
        user.setName("John Doe");
        user.setEmail("john.doe@gmail.com");
        user.setMobile("9876543210");
        user.setMemberId("100001");

        System.out.println("User: " + user);
    }
}
```

The above code prints following output:
```
User: {"name":"XXXXX XXXXX","email":"xxxxxx@gmail.com","mobile":"XXXXXXX10","member_id":"100001"}
```

# Implementing `toString`
We encourage you to implement a toString method for your class and simply call `SecureJson.toJson()` method to convert your object to the safe json. This mehtod ensures that you do not have to rely on developers to explicitly use the utility method to create a secure json and any accidental login will be secure output by default.

# Integration with Lombok
The original intention of this project was to integrate this functionality with Lombok. Lombok operates primarily at the compile time so this was not fitting the Lombok use case. Second option was to inject `toString` automatically like Lombok does but since it's way more complicated, currently we the plan is on relying on simply writing the `toString` manually.

The annotations can simply still be used in combination with Lombok but you cannot rely on the default `toString` of Lombok to to masking of the output data. Also the output is in a Lombok specific format and not a JSON. If you annotate your class with `@Data` of Lombok then you need to ensure you have your own `toString` implementation otherwise Lombok will generate it's own. Using `@ToString` of Lombok on any class that needs masked output does make any sense and should not be done.

# Jackson Annotations
Note that since this libraty uses Jackson to generate output, all the annotations and rules of Jackson will follow. For example if you want to rename the name of the property in the output, you can use `@JsonProperty` annotation. `@JsonIgnore` can be used to exclude a field from the output. 

# Providing your own `ObjectMapper`
The `SecureJson` class has number of overloaded `toJson` methods. One of the methods accepts an `ObjectMapper` that will be used instead an internal one while generating the json output. The library will add some configuration to the mapper given by you but you will be able to customize other Jackson configuration at the mapper level. Note that using any other variation of `toJson` apart from the single argument one will ignore properties at the `@SecureLog` annotation level and simply override with what is provided as parameter to the methods.
