import 'package:flutter/material.dart';

class MyLibrary extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Column(
        // center the children
        //  mainAxisAlignment: MainAxisAlignment.center,

        children: <Widget>[
          Text("My Library", style: TextStyle(fontSize: 20)),
          myBooksGride
        ],
      ),
    );
  }
}



Widget myBooksGride = new Expanded(
  child: new GridView.count(
    // Create a grid with 2 columns. If you change the scrollDirection to
    // horizontal, this produces 2 rows.
    crossAxisCount: 2,
    primary: false,
    //   padding: const EdgeInsets.all(0.0),
    // crossAxisSpacing: 10.0,
    shrinkWrap: true,
    // Generate 100 widgets that display their index in the List.
    children: List.generate(10, (index) {
      return Center(
        child: Image(image: AssetImage('assets/applogo.png')),
      );
    }),
  ),
);
