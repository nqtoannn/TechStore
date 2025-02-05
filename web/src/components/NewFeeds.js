import React, { useEffect, useState } from "react";

const NewsFeed = () => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchArticles();
  }, []);

  const fetchArticles = async () => {
    try {
        const today = new Date();
        const threeDaysAgo = new Date(today);
        threeDaysAgo.setDate(today.getDate() - 3);
        const formattedDate = threeDaysAgo.toISOString().split("T")[0];
        const response = await fetch(`https://newsapi.org/v2/everything?q=apple&from=${formattedDate}&sortBy=publishedAt&apiKey=57274a55340d4d008205d0dea1aeb2f4&language=vi`);
        const data = await response.json();

        if (data.status === "ok") {
            setArticles(data.articles); // Access the 'articles' array
        } else {
            console.error("Error in response:", data.message);
        }

        setLoading(false);
    } catch (error) {
        console.error("Error fetching articles:", error);
        setLoading(false);
    }
};


  return (
    <div className="max-w-7xl p-4 mx-auto">
      {loading ? (
        <p className="text-center text-gray-500">Loading...</p>
      ) : (
        articles.map((article, index) => (
          <div
            key={index}
            className="flex items-start mb-6 p-4 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow"
          >
            {/* Image */}
            {article.urlToImage && (
              <img
                src={article.urlToImage}
                alt={article.title}
                className="w-32 h-32 object-cover rounded-lg mr-4"
              />
            )}

            {/* Article Details */}
            <div className="flex-1">
            <h2
                className="text-xl font-semibold text-gray-800 mb-2 cursor-pointer"
                onClick={(e) => {
                    e.stopPropagation(); // Prevents the click from triggering the parent div's onClick (if any)
                    window.open(article.url, "_blank");
                }}
                >
                {article.title}
            </h2>

              <p className="text-gray-600 mb-4">{article.description}</p>

              <div className="flex items-center justify-between text-sm text-gray-500">
              <span>
                {article.author?.split(",").pop().trim()}
                </span>
                <span>{new Date(article.publishedAt).toLocaleDateString()}</span>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default NewsFeed;
