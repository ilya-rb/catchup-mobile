use crate::configuration::Settings;
use crate::domain::article::Article;
use crate::domain::news_source::{NewsSource, NewsSourceKind};
use crate::error::error_chain_fmt;
use crate::services::{dou, hacker_news, irish_times};
use actix_web::http::StatusCode;
use actix_web::{HttpResponse, ResponseError, web};
use reqwest::Client;
use serde::{Deserialize, Serialize};
use sqlx::PgPool;
use std::fmt::Formatter;

#[derive(Deserialize)]
pub struct QueryData {
    source: String,
}

#[derive(Serialize)]
pub struct Response {
    articles: Vec<Article>,
}

#[derive(thiserror::Error)]
pub enum NewsError {
    #[error("{0}")]
    UnsupportedSource(String),
    #[error(transparent)]
    UnexpectedError(#[from] anyhow::Error),
}

#[tracing::instrument(name = "Get news", skip(query, http_client, settings, db))]
pub async fn get_news(
    query: web::Query<QueryData>,
    http_client: web::Data<Client>,
    settings: web::Data<Settings>,
    db: web::Data<PgPool>,
) -> Result<HttpResponse, NewsError> {
    let key = query.source.as_str();
    let source: NewsSource = key
        .try_into()
        .map_err(|_| NewsError::UnsupportedSource(format!("Unsupported source: {}", key)))?;

    let articles = match source.kind {
        NewsSourceKind::IrishTimes => {
            irish_times::get_news(source, &http_client, &db, &settings).await
        }
        NewsSourceKind::HackerNews => hacker_news::get_news(source, &settings).await,
        NewsSourceKind::Dou => dou::get_news(source, &settings).await,
    }?;

    Ok(HttpResponse::Ok().json(web::Json(Response { articles })))
}

impl std::fmt::Debug for NewsError {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        error_chain_fmt(self, f)
    }
}

impl ResponseError for NewsError {
    fn status_code(&self) -> StatusCode {
        match self {
            NewsError::UnsupportedSource(_) => StatusCode::BAD_REQUEST,
            NewsError::UnexpectedError(_) => StatusCode::INTERNAL_SERVER_ERROR,
        }
    }
}
