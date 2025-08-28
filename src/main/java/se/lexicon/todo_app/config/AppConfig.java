package se.lexicon.todo_app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("se.lexicon.todo_app.*")
// To use NotifyUtil in your project, uncomment the line below to import the configuration from notify-util-spring module
//@Import(NotifyUtilConfig.class)
public class AppConfig {
}