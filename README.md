# automated-survey-spring

[![Build Status](https://travis-ci.org/TwilioDevEd/automated-survey-spring.svg?branch=master)](https://travis-ci.org/TwilioDevEd/automated-survey-spring)

## Deploy to Heroku

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Requirements

In order to run this application, you need to install gradle and Oracle's JDK (Tested with JDK7)

## Database setup

The application uses PostgreSQL as a database engine. In order for the application to get up and running you should
configure:

* Set environment variable `DATABASE_URL` as it's shown in the .env.example file
* If your Postgres installation doesn't have SSL support enabled, you must set `TWILIO_DISABLE_DB_SSL=true`

Running the application will also run the database migrations.

## Running the application

### Using Gradle

If you already have Gradle installed, you should be able to run the application using `gradle bootRun`. This will
install all dependencies and build the application. If no other port is specified, the app will run in port `8080`.

If you don't have gradle installed, you can use the Gradle Wrapper included in this project. Just go to the root directory
of the project and prepend any gradle task with the executable `gradlew`. So you can run the application by doing
this in the root directory of the project:

```
./gradlew bootRun
```

If you open you browser on `http://localhost:8080` you should see the list of questions that was specified in
`survey.json` file. This is the place where you can see the survey's answers after you have set up your Twilio number
to make a request to `/survey` on your server.

### Using IntelliJ IDEA

If you want to run the app within IntelliJ, you can fallow this steps.

* Open IntelliJ and select the import project option. There you can select the directory where you
cloned the repository using git or where you unpacked a copy of the project.

* In the next step you must specify that the project your are trying to import is using gradle:
 
![Import gradle project](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_select_gradle.png)

* Now you must configure your project, you should select the options to match what you can see in
the next screenshot. The Gradle JVM field depends on your local java installation.

![Configure gradle project](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_configure_project.png)

* Now the project is imported and all dependencies for it should have been already downloaded. Now
you have to configure IntelliJ to run the application. You can do this by clicking the `edit
configurations` button under the run menu:

![Edit run configurations](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_run_configurations.png)

* In this menu you must select the Application option from the left sidebar. Then you must fill in
the necessary information for your application to run as shown in the next screenshots:

![Select main class](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_select_main_class.png)

![Select main class menu](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_select_main_class_menu.png)

![Set environment variables](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_set_environment_variables.png)

![Set environment variables menu](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_set_environment_variables_menu.png)

Remember you must set `TWILIO_DISABLE_DB_SSL` only if your postgres installation doesn't have SSL
enabled. If that's not the case, you can set that variable to false, or simply not set it.

And finally, your run configuration should look like this:

![Final configuration](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_final_config.png)

* The final step is to run the application. You can do this by finding the main class in the
project explorer, and right click on it. There you can select to run the application

![Run application](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/intellij_run_application.png)

## Configuring Twilio to call your application

### Exposing the app via ngrok

For this demo it's necessary that your local application instance is
accessible from the Internet. The easiest way to accomplish this
during development is using [ngrok](https://ngrok.com/). If you're
running OS X you can install ngrok using Homebrew by running `brew
install ngrok`. First you need to make sure the application is running in you localhost.
You can run the application directly from the command line using gradle or in any IDE you like.
The previous steps describe how to run the application using Gradle or IntelliJ IDE.

After this you can expose the application to the wider Internet by
running (port 8080 is the default for Spring):

```
ngrok 8080
```

### Configuring Twilio's webhooks

You will need to provision at least one Twilio number with voice
capabilities so the application's users can take surveys. You can do
that
[here](https://www.twilio.com/user/account/phone-numbers/search). Once
you have a number you need to configure your number to work with your
application. Open
[the number management page](https://www.twilio.com/user/account/phone-numbers/incoming)
and open a number's configuration by clicking on it.

![Open a number configuration](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/number-conf.png)

Next, edit the "Request URL" field under the "Voice" section and point
it towards your ngrok-exposed application `/survey` route. Set
the HTTP method to GET. If you have are trying out the Heroku
application you need to point Twilio to
`http://<your-app-name>.herokuapp.com/survey`. See the image
below for an example:

![Webhook configuration](https://raw.github.com/TwilioDevEd/automated-survey-spring/master/screenshots/webhook-conf.png)

Give your number a call, answer the example questions and then go to:

```
http://localhost:8080/
```

The results of the survey should be there.

## Running the tests

Running the tests with gradle is as simple as executing `./gradlew test` on the command line. Just remember to set up
the correct `DATABASE_URL` environment variable as running the test will delete all the records in the database.
