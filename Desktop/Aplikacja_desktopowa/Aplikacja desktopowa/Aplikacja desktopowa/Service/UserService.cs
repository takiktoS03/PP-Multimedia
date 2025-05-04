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
            string path = "image-management-cbaee-firebase-adminsdk-fbsvc-534514b3a5.json";
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);

            // Tworzymy klienta Firestore (domyślna baza danych)
            _firestore = FirestoreDb.Create("image-management-cbaee");
        }

        public async Task<User> GetUserByEmailAsync(string email)
        {
            try
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
            catch (Exception ex)
            {
                // Log the exception or handle it as needed
                Console.WriteLine($"An error occurred: {ex.Message}");
                return null;
            }
        }
    }
}
