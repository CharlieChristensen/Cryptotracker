<h1>Cryptocoin Tracker</h1>

Playground project originally created to learn some of the Android Architecture Components. 
The app allows you to look at live prices of many types of cryptocoins as well as add create a wallet to track total portfolio value. (does not deal with actual purchasing of coins)

Some of the tech in use:
* MVVM
* Repository pattern
* Room for local persistence where screens observe database changes as the source of truth
* Websockets for live updating coin prices
* RxJava
* Navigation arch component
* Modern dagger setup with ability to use AssistedInject for constructor injection of dynamic parameters
* Dynamic theming
* ViewModel SavedState
* Gracefully handles process death

