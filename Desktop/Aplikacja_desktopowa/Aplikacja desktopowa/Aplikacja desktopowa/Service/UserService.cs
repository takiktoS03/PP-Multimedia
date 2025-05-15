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
            _firestore = FirebaseConfig.GetFirestoreDb();
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
                Console.WriteLine($"An error occurred: {ex.Message}");
                return null;
            }
        }

        public async Task<User> GetUserByUsernameAsync(string username)
        {
            try
            {
                Query query = _firestore.Collection("users").WhereEqualTo("username", username);
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
                Console.WriteLine($"An error occurred: {ex.Message}");
                return null;
            }
        }
    }
}
