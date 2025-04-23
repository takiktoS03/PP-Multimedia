using Aplikacja_desktopowa.Model;
using Google.Apis.Auth.OAuth2;
using Google.Cloud.Firestore;
using Google.Cloud.Firestore.V1;
using Grpc.Auth;
using Grpc.Core;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    public class UserService
    {
        private readonly FirestoreDb _firestore;

        public UserService()
        {
            string path = "image-management-cbaee-firebase-adminsdk-fbsvc-5499d8a881.json";
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);

            // Tworzymy klienta Firestore (domyślna baza danych)
            FirestoreDb db = FirestoreDb.Create("image-management-cbaee");
        }

        public async Task<User> GetUserByEmailAsync(string email)
        {
            Query query = _firestore.Collection("users").WhereEqualTo("email", email);
            QuerySnapshot snapshot = await query.GetSnapshotAsync();

            foreach (var doc in snapshot.Documents)
            {
                if (doc.Exists)
                {
                    return doc.ConvertTo<User>();
                }
            }

            return null;
        }
    }
}
