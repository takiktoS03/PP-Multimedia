const functions = require("firebase-functions");
const nodemailer = require("nodemailer");

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "mediamenager1@gmail.com",
    pass: "avue ydyf bmpm kyql"
  }
});

exports.sendCode = functions.https.onRequest((req, res) => {
  console.log("Odebrano request:", req.body);

  const email = req.body.email;
  const code = req.body.code;

  if (!email || !code) {
    console.error("Brakuje email lub kodu.");
    return res.status(400).send("Brakuje danych");
  }

  const mailOptions = {
    from: "MultiMediaApp <mediamenager1@gmail.com>",
    to: email,
    subject: "Twój kod weryfikacyjny",
    text: `Twój kod weryfikacyjny to: ${code}`
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.error("Błąd:", error);
      return res.status(500).send("Błąd wysyłki");
    } else {
      console.log("E-mail wysłany:", info.response);
      return res.status(200).send("OK");
    }
  });
});