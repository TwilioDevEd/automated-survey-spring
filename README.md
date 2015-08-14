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

If you already have Gradle installed, you should be able to run the application using `gradle bootRun`. This will
install all dependencies and build the application. If no other port is specified, the app will run in port `8080`.

If you open you browser on `http://localhost:8080` you should see the list of questions that was specified in
`survey.json` file. This is the place where you can see the survey's answers after you have set up your Twilio number
to make a request to `/survey` on your server.

### Configuring Twilio to call your application

#### Exposing the app via ngrok

For this demo it's necessary that your local application instance is
accessible from the Internet. The easiest way to accomplish this
during development is using [ngrok](https://ngrok.com/). If you're
running OS X you can install ngrok using Homebrew by running `brew
install ngrok`. First you will need to run the application:

```
gradle bootRun
```

After this you can expose the application to the wider Internet by
running (port 8080 is the default for Spring):

```
ngrok 8080
```

#### Configuring Twilio's webhooks

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

Give your number a call, answer the questions about bears and then go to:

```
http://localhost:8080/
```

The results of the survey should be there.

### Running the tests

Running the tests with gradle is as simple as executing `gradle test` on the command line. Just remember to set up
the correct `DATABASE_URL` environment variable as running the test will delete all the records in the database.
