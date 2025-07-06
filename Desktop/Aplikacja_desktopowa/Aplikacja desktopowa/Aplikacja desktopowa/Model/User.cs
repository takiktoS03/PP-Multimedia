using Google.Cloud.Firestore;
using System;

namespace Aplikacja_desktopowa.Model
{
    /// <summary>
    /// Reprezentuje użytkownika w aplikacji. Mapowana do dokumentu Firestore.
    /// </summary>
    [FirestoreData]
    public class User
    {
        /// <summary>
        /// Adres e-mail użytkownika.
        /// </summary>
        [FirestoreProperty]
        public string Email { get; set; }

        /// <summary>
        /// Zhashowane hasło użytkownika.
        /// </summary>
        [FirestoreProperty(Name = "password_hash")]
        public string PasswordHash { get; set; }

        /// <summary>
        /// Imię użytkownika.
        /// </summary>
        [FirestoreProperty]
        public string Name { get; set; }

        /// <summary>
        /// Rola przypisana użytkownikowi (np. "admin", "user").
        /// </summary>
        [FirestoreProperty(Name = "role")]
        public string Role { get; set; }

        /// <summary>
        /// Data utworzenia konta.
        /// </summary>
        [FirestoreProperty(Name = "created_at")]
        public Timestamp CreatedAt { get; set; }

        /// <summary>
        /// Data ostatniej aktualizacji danych użytkownika (może być null).
        /// </summary>
        [FirestoreProperty(Name = "updated_at")]
        public Timestamp? UpdatedAt { get; set; }

        /// <summary>
        /// Unikalny identyfikator użytkownika.
        /// </summary>
        [FirestoreProperty(Name = "user_id")]
        public string Id { get; set; }

        /// <summary>
        /// Określa, czy e-mail użytkownika został zweryfikowany.
        /// </summary>
        [FirestoreProperty(Name = "isVerified")]
        public bool IsVerified { get; set; }

        /// <summary>
        /// Kod weryfikacyjny wysłany użytkownikowi do potwierdzenia adresu e-mail.
        /// </summary>
        [FirestoreProperty(Name = "verification_code")]
        public string VerificationCode { get; set; }
    }
}
