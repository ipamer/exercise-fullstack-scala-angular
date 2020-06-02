# Assessment: Trending Meetup locations 
We love attending meetups, and also love organising them. We love them so much we are willing to relocate for them.
With the huge amount of meetups taking place on a daily basis, it can be hard to pinpoint where to fly to next.
Therefore, for this assessment, you will build a distributable solution to discover trending Meetup locations. 

## Assignment
The assignment is to create a back-end and front-end application which can retrieve, process and display the most popular meetup locations.
For this, we ask you to connect to the meetup.com [RSVP stream](http://meetup.github.io/stream/rsvpTicker/).
An RSVP to a meetup is a sign that a person will be attending a meetup (and thus indicates interest in a topic).
Meetup has a nice example web page where you can see RSVP's arriving in real-time [here](http://meetup.github.io/stream/rsvpTicker/)

## Implementation
Although we tend to use JVM-oriented languages and while we appreciate people straying off the beaten path,
for this assignment we will limit the implementation language of the back-end to any strongly and statically typed language.

How you decide to set it up is completely up to you, but we would like to see the following features being supported:
1. Coupling the implementation to the stream
1. Calculating the most popular meetup locations in the world
1. A front-end showing a heatmap overlay of the most popular locations on a map

We ask you to create a solution which is built with scalability in mind and adheres to the [Reactive Manifesto](https://www.reactivemanifesto.org/).

## Data
We have prepared a small [sample set](meetup.json) of data for you to work with.
This data is in the format delivered by the [Meetup Stream API](https://www.meetup.com/meetup_api/docs/stream/2/rsvps).
The sample set is a json file containing a single RSVP 'message' per line.
A single RSVP looks like this (but without the line-breaks and pretty formatting):
```
{
  "venue": {
    "venue_name": "Brisbane Workers\u2019 Community Centre",
    "lon": 153.002182,
    "lat": -27.46052,
    "venue_id": 17770652
  },
  "visibility": "public",
  "response": "no",
  "guests": 0,
  "member": {
    "member_id": 221294483,
    "photo": "http:\/\/photos1.meetupstatic.com\/photos\/member\/e\/8\/0\/4\/thumb_263939396.jpeg",
    "member_name": "Jenny Lethbridge"
  },
  "rsvp_id": 1658874890,
  "mtime": 1489923634000,
  "event": {
    "event_name": "Guest Presenter: Valerie Perret - Wellness facilitator and Self-Care Afficionado",
    "event_id": "238486635",
    "time": 1491525000000,
    "event_url": "https:\/\/www.meetup.com\/DreamBuilders-Brisbane\/events\/238486635\/"
  },
  "group": {
    "group_topics": [
      {
        "urlkey": "metaphysics",
        "topic_name": "Metaphysics"
      },
      {
        "urlkey": "consciousness",
        "topic_name": "Consciousness"
      },
      {
        "urlkey": "lifetransform",
        "topic_name": "Life Transformation"
      },
      {
        "urlkey": "wellness",
        "topic_name": "Wellness"
      },
      {
        "urlkey": "positive-thinking",
        "topic_name": "Positive Thinking"
      },
      {
        "urlkey": "personal-development",
        "topic_name": "Personal Development"
      },
      {
        "urlkey": "spiritual-growth",
        "topic_name": "Spiritual Growth"
      },
      {
        "urlkey": "life-coaching",
        "topic_name": "Life Coaching"
      },
      {
        "urlkey": "self-awareness",
        "topic_name": "Self-Awareness"
      },
      {
        "urlkey": "self-exploration",
        "topic_name": "Self Exploration"
      },
      {
        "urlkey": "self-empowerment",
        "topic_name": "Self-Empowerment"
      },
      {
        "urlkey": "self-love-and-self-acceptance",
        "topic_name": "Self love and Self acceptance"
      },
      {
        "urlkey": "self-esteem-self-confidence-boundaries-limits",
        "topic_name": "Self Esteem Self Confidence Boundaries & Limits"
      },
      {
        "urlkey": "self-help-self-improvement",
        "topic_name": "Self Help & Self Improvement"
      }
    ],
    "group_city": "Brisbane",
    "group_country": "au",
    "group_id": 19966923,
    "group_name": "Expand Your Awareness & Unlock Your Dreams   Brisbane",
    "group_lon": 153.02,
    "group_urlname": "DreamBuilders-Brisbane",
    "group_lat": -27.46
  }
}
```
__IMPORTANT__
As this is data from the real world, you may find imperfections in it.
We used the Meetup API to collect the data, so you may find unexpected line-endings, or incomplete json objects.
Be sure to handle these kind of anomalies!

## Bonus
Some ideas we have about extending the base assignment:
 - a Dockerized solution
 - persisting data
 - use a streaming processing framework e.g Apache Flink, Apache Spark
 - make the map interactive (e.g. when clicking on a country show tranding topics)

Please do not limit yourself to what we can come up with, we love being surprised by your awesome ideas! 
