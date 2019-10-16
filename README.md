# Requirements

## Usage

Usage is as simple as passing the player and the requirements section you wish to process

```java
RequirementsUtil.handle(Player, ConfigurationSection)
```

## Registering requirement processors

RequirementsUtil also exposes the ability to register your own custom requirements by implementing
the `RequirementsProcessor` interface,

```java
public class PermissionProcessor implements RequirementsProcessor {

    public boolean checkMatch(@NotNull Player player, @NotNull ConfigurationSection requirement) {
        return player.hasPermission(requirement.getString("permission"));
    }
}
```

for example:

```java
public class Example {
    public void register() { 
        RequirementsUtil.registerProcessor("HAS_PERMISSION", new PermissionProcessor());
    }
}


```
