import 'package:flutter/material.dart';
import 'login.dart';
import 'browsestore.dart';
import 'settings.dart';
import 'mylibrary.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'splash.dart';
import 'dart:developer' as developer;


void main() {
  runApp(MaterialApp(
    title: 'My app', // used by the OS task switcher

    initialRoute: '/',
    theme: ThemeData(primaryColor: Colors.orange),

    routes: {
      // When navigating to the "/" route, build the FirstScreen widget.
      '/': (context) => SplashPage(),
      '/home': (context) => HomeScreen(),

      '/login': (context) => SignInPage(),
      // When navigating to the "/second" route, build the SecondScreen widget.
    },
  ));
}
final FirebaseAuth _auth = FirebaseAuth.instance;

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreen createState() => _HomeScreen();
}

class _HomeScreen extends State<HomeScreen> {
  int _selectedIndex = 0;
  static const TextStyle optionStyle =
      TextStyle(fontSize: 30, fontWeight: FontWeight.bold);

  List<Widget> _widgetOptions = [MyLibrary(), BrowseStore(), Settings()];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('BitVedas'),
      ),
      body: Center(
        child: _widgetOptions.elementAt(_selectedIndex),
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard),
            title: Text('My Library'),
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.store),
            title: Text('Browse Store'),
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.settings),
            title: Text('Settings'),
          ),
        ],
        currentIndex: _selectedIndex,
        selectedItemColor: Colors.amber[800],
        onTap: _onItemTapped,
      ),
    );
  }


String userName;
String phoneNumber;
String accountUniqueID;
  @override
  void initState() {
    super.initState();
    developer.log("Start up init");
    getUser().then((user) {
      if (user != null) {
        phoneNumber=user.phoneNumber;
accountUniqueID=phoneNumber.replaceAll("+", "x");
        developer.log("Phone Number: "+phoneNumber+"\n UID: "+accountUniqueID);
      }
    });
  }

  Future<FirebaseUser> getUser() async {
    return await _auth.currentUser();
  }


}
