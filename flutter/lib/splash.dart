import 'package:bitvedas/old/main%20-%20Copy.dart';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'dart:developer' as developer;

class SplashPage extends StatefulWidget {
//  SplashPage({Key key}) : super(key: key);

  @override
  _SplashPageState createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  @override
  initState() {
    FirebaseAuth.instance
        .currentUser()
        .then((currentUser) => {
              if (currentUser == null)
                {
                  Navigator.pushReplacementNamed(context, "/login"),
                  developer.log("not Logged in"),
                }
              else
                {
//                  Navigator.pushReplacementNamed(context, "/home"),
//                  Navigator.pushReplacement(context,
                  //                    MaterialPageRoute(builder: (context) => HomeScreen())),
//Navigator.pushReplacement(context, MaterialPageRoute(builder: (BuildContext context) => HomeScreen())),
                  Navigator.pushReplacement(context,
                      MaterialPageRoute(builder: (context) => HomeScreen())),
                  developer.log("Logged in"),
                  /*Firestore.instance
                      .collection("users")
                      .document(currentUser.uid)
                      .get()
                      .then((DocumentSnapshot result) =>
                          Navigator.pushReplacement(
                              context,
                              MaterialPageRoute(
                                  builder: (context) => HomePage(
                                        title: result["fname"] + "'s Tasks",
                                        uid: currentUser.uid,
                                      ))))
                      .catchError((err) => print(err))
                      */
                }
            })
        .catchError((err) => print(err));
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Container(
          child: Text("Loading App..."),
        ),
      ),
    );
  }
}
