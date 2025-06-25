using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Model
{
    [FirestoreData]
    public class User
    {
        [FirestoreProperty]
        public string Email { get; set; }

        [FirestoreProperty(Name = "password_hash")]
        public string PasswordHash { get; set; }

        [FirestoreProperty]
        public string Name { get; set; }

        [FirestoreProperty(Name = "created_at")]
        public Timestamp CreatedAt { get; set; }

        [FirestoreProperty(Name = "updated_at")]
        public Timestamp? UpdatedAt { get; set; }

        [FirestoreProperty(Name = "user_id")]
        public string Id { get; set; }

        [FirestoreProperty(Name = "isVerified")]
        public bool IsVerified { get; set; }

        [FirestoreProperty(Name = "verification_code")]
        public string VerificationCode { get; set; }
    }
}
