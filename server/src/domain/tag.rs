use anyhow::{Result, bail};
use serde::{Deserialize, Deserializer, Serialize};

#[derive(Debug, PartialEq, Serialize, Clone)]
pub struct Tag(pub String);

#[derive(Debug, PartialEq, Serialize, Clone)]
pub struct Tags(pub Vec<Tag>);

impl Tag {
    pub fn new(value: String) -> Result<Tag> {
        if value.is_empty() {
            bail!("Tag cannot be empty");
        }
        Ok(Tag(value))
    }
}

impl<'a> Deserialize<'a> for Tag {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: Deserializer<'a>,
    {
        let s = String::deserialize(deserializer)?;
        Tag::new(s).map_err(serde::de::Error::custom)
    }
}

impl From<Tags> for Vec<String> {
    fn from(value: Tags) -> Self {
        value.0.into_iter().map(|t| t.0).collect()
    }
}
