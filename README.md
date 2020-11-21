<div align="center">

## Hotpur

A fork of Purpur that aims to improve performance and add FabricMC compatibility. 

</div>

## Contact
Join us on [Discord](https://discord.gg/Kdy8NM5HW4)

## Downloads
Coming soon

## License
Everything is licensed under the MIT license, and is free to be used in your own fork.

See [starlis/empirecraft](https://github.com/starlis/empirecraft) and [electronicboy/byof](https://github.com/electronicboy/byof) 
for the license of material used/modified by this project.

## bStats
Coming soon

## API

Hotpur API maven dependency:
```
<dependency>
    <groupId>com.ishland.hotpur</groupId>
    <artifactId>hotpur-api</artifactId>
    <version>1.16.4-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Hotpur API gradle dependency:
```
compileOnly 'com.ishland.hotpur:hotpur-api:1.16.4-R0.1-SNAPSHOT'
```

Yes, this also includes all API provided by Paper, Spigot, and Bukkit.

## Building and setting up

#### Initial setup
Run the following commands in the root directory:

```
./hotpur jar
```

#### Creating a patch
Patches are effectively just commits in either `Hotpur-API` or `Hotpur-Server`. 
To create one, just add a commit to either repo and run `./hotpur rbp`, and a 
patch will be placed in the patches folder. Modifying commits will also modify its 
corresponding patch file.

See [CONTRIBUTING.md](CONTRIBUTING.md) for more detailed information.


#### Compiling

Use the command `./hotpur build` to build the api and server. Compiled jars
will be placed under `Hotpur-API/target` and `Hotpur-Server/target`.

To get a paperclip jar, run `./hotpur jar`.
