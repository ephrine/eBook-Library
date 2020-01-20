import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:firebase_auth/firebase_auth.dart';

final FirebaseAuth _auth = FirebaseAuth.instance;
bool userSignedin = false;
void checkAuth() async {
  final FirebaseUser user = await _auth.currentUser();
  if (user == null) {
    userSignedin = true;

    return;
  } else {
    userSignedin = false;
  }
}

void main() => runApp(SplashPage());

//Splash

class SplashPage extends StatefulWidget {
  SplashPage({Key key}) : super(key: key);

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
//                  Navigator.pushReplacementNamed(context, "/login")
                  LoginScreen()
                }
              else
                {MyApp()}
            })
        .catchError((err) => print(err));
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Container(
          child: Text("Loading..."),
        ),
      ),
    );
  }
}

//Login

String _message = '';
String _verificationId;

final TextEditingController _phoneNumberController = TextEditingController();
final TextEditingController _smsController = TextEditingController();

class SignInPage extends StatefulWidget {
  final String title = 'Registration';
  //List<Widget> _layoutsView = [];

  @override
  State<StatefulWidget> createState() => SignInPageState();
}

class SignInPageState extends State<SignInPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        actions: <Widget>[
          Builder(builder: (BuildContext context) {
            return FlatButton(
              child: const Text('Sign out'),
              textColor: Theme.of(context).buttonColor,
              onPressed: () async {
                final FirebaseUser user = await _auth.currentUser();
                if (user == null) {
                  Scaffold.of(context).showSnackBar(const SnackBar(
                    content: Text('No one has signed in.'),
                  ));
                  return;
                } else {}
                _signOut();
                final String uid = user.uid;
                Scaffold.of(context).showSnackBar(SnackBar(
                  content: Text(uid + ' has successfully signed out.'),
                ));
              },
            );
          })
        ],
      ),
      body: Builder(builder: (BuildContext context) {
        return ListView(
          scrollDirection: Axis.vertical,
          children: <Widget>[
            _PhoneSignInSection(Scaffold.of(context)),
          ],
        );
      }),
    );
  }

  // Example code for sign out.
  void _signOut() async {
    await _auth.signOut();
  }
}

class _PhoneSignInSection extends StatefulWidget {
  _PhoneSignInSection(this._scaffold);

  final ScaffoldState _scaffold;
  @override
  State<StatefulWidget> createState() => _PhoneSignInSectionState();
}

class _PhoneSignInSectionState extends State<_PhoneSignInSection> {
  bool showVerifyCode = false;

  @override
  Widget build(BuildContext context) {
    Widget screen;
    if (showVerifyCode == false) {
      screen = enterPhoneNo();
    } else if (showVerifyCode) {
      screen = enterVerificationCode();
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[screen],
    );
  }

  Widget enterPhoneNo() {
    return Container(
        child: Column(
      children: <Widget>[
        Container(
          child: const Text('Sign in with Phone Number'),
          padding: const EdgeInsets.all(16),
          alignment: Alignment.center,
        ),
        TextFormField(
          controller: _phoneNumberController,
          decoration: const InputDecoration(
              labelText: 'Phone number (+x xxx-xxx-xxxx)'),
          validator: (String value) {
            if (value.isEmpty) {
              return 'Phone number (+x xxx-xxx-xxxx)';
            }
            return null;
          },
        ),
        Container(
          padding: const EdgeInsets.symmetric(vertical: 16.0),
          alignment: Alignment.center,
          child: RaisedButton(
            onPressed: () async {
              showVerifyCode = true;

              _verifyPhoneNumber();
            },
            child: const Text('Verify phone number'),
          ),
        ),
      ],
    ));
  }

  Widget enterVerificationCode() {
    return Column(children: <Widget>[
      TextField(
        controller: _smsController,
        decoration: const InputDecoration(labelText: 'Verification code'),
      ),
      Container(
        padding: const EdgeInsets.symmetric(vertical: 16.0),
        alignment: Alignment.center,
        child: RaisedButton(
          onPressed: () async {
            _signInWithPhoneNumber();
          },
          child: const Text('Sign in with phone number'),
        ),
      ),
      Container(
        alignment: Alignment.center,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Text(
          _message,
          style: TextStyle(color: Colors.red),
        ),
      )
    ]);
  }

  // Example code of how to verify phone number
  void _verifyPhoneNumber() async {
    setState(() {
      _message = '';
    });
    final PhoneVerificationCompleted verificationCompleted =
        (AuthCredential phoneAuthCredential) {
      _auth.signInWithCredential(phoneAuthCredential);
      setState(() {
        _message = 'Received phone auth credential: $phoneAuthCredential';
      });
    };

    final PhoneVerificationFailed verificationFailed =
        (AuthException authException) {
      setState(() {
        _message =
            'Phone number verification failed. Code: ${authException.code}. Message: ${authException.message}';
      });
    };

    final PhoneCodeSent codeSent =
        (String verificationId, [int forceResendingToken]) async {
      widget._scaffold.showSnackBar(const SnackBar(
        content: Text('Please check your phone for the verification code.'),
      ));
      _verificationId = verificationId;
    };

    final PhoneCodeAutoRetrievalTimeout codeAutoRetrievalTimeout =
        (String verificationId) {
      _verificationId = verificationId;
    };

    await _auth.verifyPhoneNumber(
        phoneNumber: _phoneNumberController.text,
        timeout: const Duration(seconds: 130),
        verificationCompleted: verificationCompleted,
        verificationFailed: verificationFailed,
        codeSent: codeSent,
        codeAutoRetrievalTimeout: codeAutoRetrievalTimeout);
  }

  // Example code of how to sign in with phone.
  void _signInWithPhoneNumber() async {
    final AuthCredential credential = PhoneAuthProvider.getCredential(
      verificationId: _verificationId,
      smsCode: _smsController.text,
    );
    final FirebaseUser user =
        (await _auth.signInWithCredential(credential)).user;
    final FirebaseUser currentUser = await _auth.currentUser();
    assert(user.uid == currentUser.uid);
    setState(() {
      if (user != null) {
        _message = 'Successfully signed in, uid: ' + user.uid;
      } else {
        _message = 'Sign in failed';
      }
    });
  }
}

//Main App
/// This Widget is the main application widget.
class MyApp extends StatelessWidget {
  static const String _title = 'BitVedas';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: HomeScreen(),
      /*localizationsDelegates: [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        FFULocalizations.delegate,
      ],
      supportedLocales: [
        const Locale('en', 'US'),
      ], */
      theme: ThemeData(primaryColor: Colors.orange),
    );
  }
}

class LoginScreen extends StatefulWidget {
  LoginScreen({Key key}) : super(key: key);

  @override
  _LoginScreen createState() => _LoginScreen();
}

class _LoginScreen extends State<LoginScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        /* appBar: AppBar(
        title: const Text('BitVedas'),
      ),*/

        body: Stack(
      children: <Widget>[emptySpace(), buttonLogin(context)],
    ));
  }
}

Widget emptySpace() {
  return Container(
      margin: const EdgeInsets.only(top: 120),
      child: Column(
        children: <Widget>[
          applogo(),
          Center(
              child: new Text(
            "\n We provide free access to Science Textbooks of various category like medicine, pharmacy, chemistry, biochemistry, Ayurveda and many more.",
            textAlign: TextAlign.center,
          )),
        ],
      ));
}

Widget applogo() {
  return Container(
      child: Row(
    mainAxisAlignment: MainAxisAlignment.center,
    children: const <Widget>[
      Icon(
        Icons.android,
        color: Colors.blue,
        size: 36.0,
      ),
    ],
  ));
}

Widget buttonLogin(context) {
  return Container(
    child: Center(
      child: Container(
          child: Align(
            alignment: Alignment.bottomCenter,
            child: RaisedButton(
              onPressed: () {
                // Validate will return true if the form is valid, or false if
                // the form is invalid.
                /*          Navigator.push(
    context,
    MaterialPageRoute(builder: (context) => SignInPage()),
  );*/
              },
              child: Text('Submit'),
            ),
          ),
          margin: const EdgeInsets.only(bottom: 50)),
    ),
  );
}

class HomeScreen extends StatefulWidget {
  HomeScreen({Key key}) : super(key: key);

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
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
}

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

class BrowseStore extends StatefulWidget {
  BrowseStore({Key key}) : super(key: key);

  @override
  _BrowseStore createState() => _BrowseStore();
}

class _BrowseStore extends State<BrowseStore> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        child: const WebView(
          initialUrl: 'https://www.google.con',
          javascriptMode: JavascriptMode.unrestricted,
        ),
      ),
    );
  }
}

class Settings extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        child: Center(
          child: Column(
            // center the children
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Icon(
                Icons.favorite,
                size: 160.0,
                color: Colors.white,
              ),
              Text(
                "3 Tab",
                style: TextStyle(color: Colors.white),
              ),
              RaisedButton(
                  onPressed: () {
                    // Validate will return true if the form is valid, or false if
                    // the form is invalid.
                  },
                  child: Text("Login"))
            ],
          ),
        ),
      ),
    );
  }
}

Column _buildButtonColumn(Color color, IconData icon, String label) {
  return Column(
    mainAxisSize: MainAxisSize.min,
    mainAxisAlignment: MainAxisAlignment.center,
    children: [
      Icon(icon, color: color),
      Container(
        margin: const EdgeInsets.only(top: 8),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w400,
            color: color,
          ),
        ),
      ),
    ],
  );
}

Widget buttonSection = Container(
  child: Row(
    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
    children: [
      _buildButtonColumn(Colors.blueAccent, Icons.call, 'CALL'),
    ],
  ),
);

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
