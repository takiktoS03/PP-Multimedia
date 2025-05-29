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
using Google.Cloud.Firestore;

namespace Aplikacja_desktopowa.Service
{
    public class UserService
    {
        private readonly FirestoreDb _firestore;
        private readonly CollectionReference _usersCollection;

        public UserService()
        {
            _firestore = FirebaseConfig.GetFirestoreDb();
            var db = FirebaseConfig.GetFirestoreDb();
            _usersCollection = db.Collection("users");
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

        public async Task AddUserAsync(User user)
        {
            await _usersCollection.AddAsync(user);
        }

        public async Task MarkEmailAsVerified(string email)
        {
            var snapshot = await _usersCollection.WhereEqualTo("email", email).GetSnapshotAsync();
            if (snapshot.Documents.Count > 0)
            {
                var docRef = snapshot.Documents[0].Reference;
                await docRef.UpdateAsync(new Dictionary<string, object>
        {
            { "isVerified", true },
            { "verification_code", FieldValue.Delete }
        });
            }
        }
    }
}
