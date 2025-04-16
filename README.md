<<<<<<< HEAD
# Platoon

Platoon
Team Name - Team.
Members - Kerby Dela Cruz (just me)

Platoon is a game where a dealer and player are given 10 cards. They then must make
5 piles from those 10 cards to fight eachother. Each draws a card to decide who goes first
then take turns picking a pile from each side to battle. The higher value wins with
face cards having special values and effects. The first to 3 hand wins wins the round.

Kings will always win the hand, unless it's against a Queen, who will always lose the
hand unless there's a king. The Jack will swap the hands meaning that the value of your
opponent's hand will now be yours and vice versa. If Kings or Queens are opposing
eachother, The higher value hand wins, if the values are the same, it will be a draw

To my knowledge this game was in an optional side area as a mini game in ni no kuni,
a game from over 10 years ago. There isn't much coverage on this mini game and I feel
it's a fun card game that people should play more often. Along with my added rules.

This was my initial UML diagram
![image](https://github.com/user-attachments/assets/2017a64d-e215-48a2-8c7c-91f77207263a)
The plan is to start off with the basic game mechanics, making sure that the hands
interact with eachother correctly, then the visuals, cards, background, score UI.
Finally, add some interactive feedback (i.e. cards popping up when selected) to
make the game less stiff. I will be doing this all on my own.


This is my final UML diagram
<img width="707" alt="Image" src="https://github.com/user-attachments/assets/767ec3ba-8f98-4e0e-ad3c-aa4d41b54b97" />

This game requires Java and JavaFX to run, make sure you have both downloaded.
Clone or download the repository on this page
open the Platoon folder using VSCode and type this into the terminal to compile the game
javac --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics Game/Main.java

Run the game using

java --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics Game.Main

(or just hit launch in vscode)

make sure to replace "/path/to/javafx-sdk/lib" with your own path to the JavaFX SDK library





=======
## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
>>>>>>> ab4a00e (platoon commit)
